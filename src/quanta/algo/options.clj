(ns quanta.algo.options
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [com.rpl.specter :as specter]))

(defn apply-options
  "returns algo with options applied 
   options is a map, keys are paths (vectors)
   and values are the values to set."
  [algo options]
  ; if all paths are keys, this is really simple.
  ; (update template :algo merge options)
  ; but if we can have hierarchical paths, then we 
  ; need to set them via specter, so type gets
  ; preserved. 
  (try (reduce
        (fn [r [path v]]
          (let [path (if (keyword? path)
                       [path]
                       path)]
            ;(debug "setting path: " path " to val: " v)
            (specter/setval path v r)))
        algo
        options)
       (catch Exception ex
         (error "algo-apply-options"
                {:algo algo
                 :options options}
                ex)
         (throw ex))))

;; VARIATIONS

(defn- add-key [m [k seq]]
  (map #(assoc m k %) seq))

(defn- map-keys [r key-seq-tuples]
  (let [seq (add-key r (first key-seq-tuples))
        next (rest key-seq-tuples)]
    (if (empty? next)
      seq
      (flatten (map #(map-keys % next) seq)))))

(defn make-variations
  "returns a seq of different options that each can be
   applied to an algo

   example:
     (make-variations [:x [1 2 3] 
                       :y [:a :b :c]
                       :debug [true false]])
   returns:
    ;; => ({:x 1, :y :a, :debug true}
  ;;     {:x 1, :y :a, :debug false}
  ;;     {:x 1, :y :b, :debug true}
  ;;     {:x 1, :y :b, :debug false}
  ;;     {:x 1, :y :c, :debug true}
  ;;     {:x 1, :y :c, :debug false}
  ;;     {:x 2, :y :a, :debug true}
  ;;     {:x 2, :y :a, :debug false}
  ;;     {:x 2, :y :b, :debug true}
  ;;     {:x 2, :y :b, :debug false}
  ;;     {:x 2, :y :c, :debug true}
  ;;     {:x 2, :y :c, :debug false}
  ;;     {:x 3, :y :a, :debug true}
  ;;     {:x 3, :y :a, :debug false}
  ;;     {:x 3, :y :b, :debug true}
  ;;     {:x 3, :y :b, :debug false}
  ;;     {:x 3, :y :c, :debug true}
  ;;     {:x 3, :y :c, :debug false})"
  [variation-spec]
  (let [key-seq-tuples
        (if (map? variation-spec)
          (map concat variation-spec)
          (partition 2 variation-spec))]
    (map-keys {} key-seq-tuples)))

(defn create-algo-variations
  "input: template and variation spec
   returns a templates, with different option
   variations applied. example:
   (create-template-variations t :asset [:EURUSD :SPY :BTCUSD]
                        :n [100 200 500])"
  [algo variation-spec]
  (let [option-seq (make-variations variation-spec)]
    (map (partial apply-options algo) option-seq)))

(defn variation-keys [variation-spec]
  (if (map? variation-spec)
    (keys variation-spec)
    (->> (partition 2 variation-spec)
         (map first))))

(comment

  ;; VARIATIONS

  (add-key {:calendar [:us :d]}
           [:asset ["a" "b" "c"]])
  (map-keys {:calendar [:us :d]}
            [[:asset ["a" "b" "c"]]
             [:n [100 500 1000]]])

; option-ui => algo
  (def paths [:a [:b :c] :d])
  (def data [{:a 1 :b {:c 22 :x 5} :d 55}
             {:x 1 :i {:y 2 :x 5} :d 55}])

  (specter/select [0 :b :c] data)
  (specter/setval [0 :b :c] 555 data)

  (specter/setval [0 :b :c] 555 [])

  (specter/select [0 :b :c] data)

  (defn no-path? [p]
    (info "no-path: " p)
    (not (contains? paths p)))

  (defn path? [p]
    (info "path: " p)
    (contains? paths p))

  (no-path? :d)

  (specter/setval [:a specter/ALL] 4 data)

  (specter/transform [0 :b :c]
                     specter/NONE
                     data)

  (specter/select [:a :b] data)

  (specter/setval [1 :asset]  "NZD/USD"
                  [:day {:feed :fx
                         :asset "EUR/USD"}
                   :minute {:type :trailing-bar, :asset "EUR/USD", :import :kibot-http,
                            :trailing-n 1440, :max-open-close-over-low-high 0.3, :volume-sma-n 30}
                   :signal {:formula [:day :minute], :spike-atr-prct-min 0.5, :pivot-max-diff 0.001,
                            :algo 'juan.algo.combined/daily-intraday-combined}])

;  
  )
