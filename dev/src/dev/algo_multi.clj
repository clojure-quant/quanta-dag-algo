(ns dev.algo-multi
  (:require
   [missionary.core :as m]
   [quanta.dag.env :refer [log]]
   [quanta.algo.options :refer [apply-options]]
   [quanta.algo.dag.spec :refer [spec->ops]]))

(defn multi-calc-d [env opts dt]
  (log env "** multi-calc-d " dt)
  {:d dt :opts opts})

(defn multi-calc-m [env opts dt]
  (log env "** multi-calc-m " dt)
  {:m dt :opts opts})

(defn multi-signal [env opts d m]
  (log env "** multi-signal " {:day d :min m})
  (vector d m))

(defn multi-signal-raw [opts input-cells]
  (println "creating multi-signal-raw cell: " opts "input cells: " input-cells)
  (let [formula-fn (fn [d m]
                     (println "** multi-signal-raw " {:day d :min m})
                     (vector d m))
        formula-cell (apply m/latest formula-fn input-cells)]
    (m/signal formula-cell)))

(def multi-algo
  [{:asset "BTCUSDT"} ; this options are global
   :day {:calendar [:crypto :d]
         :fn  multi-calc-d
         :env? true
         :x 2}
   :min {:calendar [:crypto :m]
         :fn multi-calc-m
         :env? true
         :y 5}
   :signal {:formula [:day :min]
            :fn multi-signal
            :env? true
            :z 27}
   :signal2 {:formula-raw [:day :min]
             :fn multi-signal-raw
             :z 27}])

(spec->ops multi-algo)

;; => [[:day
;;      {:calendar [:crypto :d],
;;       :algo-fn #function[dev.algo-multi/multi-calc],
;;       :opts {:asset "BTCUSDT", :calendar [:crypto :d], :x 2}}]
;;     [:min
;;      {:calendar [:crypto :m],
;;       :algo-fn #function[dev.algo-multi/multi-calc],
;;       :opts {:asset "BTCUSDT", :calendar [:crypto :m], :y 5}}]
;;     [:signal
;;      {:formula [:day :min],
;;       :algo-fn #function[dev.algo-multi/multi-signal],
;;       :opts {:asset "BTCUSDT", :formula [:day :min], :z 27}}]]

(apply-options multi-algo {[2 :x] 2
                           [4 :y] :m
                           [6 :z] 90})
;; => [{:asset "BTCUSDT"}
;;     :day
;;     {:calendar [:crypto :d], :algo #function[dev.algo-multi/multi-calc], :x 2}
;;     :min
;;     {:calendar [:crypto :m], :algo #function[dev.algo-multi/multi-calc], :y :m}
;;     :signal
;;     {:formula [:day :min], :algo #function[dev.algo-multi/multi-signal], :z 90}]


