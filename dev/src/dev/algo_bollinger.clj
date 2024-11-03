(ns dev.algo-bollinger
  (:require
   [taoensso.telemere :as t]
   [missionary.core :as m]
   [tech.v3.datatype :as dtype]
   [tablecloth.api :as tc]
   [ta.indicator.band :as band]
   [ta.indicator.signal :refer [cross-up]]
   [quanta.dag.env :refer [log]]
   [quanta.bar.env :refer [get-trailing-bars]]
   [quanta.algo.dag.spec :refer [spec->ops]]
   [quanta.algo.options :refer [apply-options]]))

(defn entry-one [long short]
  (cond
    long :long
    short :short
    :else :flat))

(defn bollinger-calc [env opts bar-ds]
  (log env "bollinger-opts: " opts)
  (let [n (or (:atr-n opts) 2)
        k (or (:atr-k opts) 1.0)
        ;_ (log "trailing-bars: " ds-bars) ; for debugging - logs to the dag logfile
        ds-bollinger (band/add-bollinger {:n n :k k} bar-ds)
        long-signal (cross-up (:close ds-bollinger) (:bollinger-upper ds-bollinger))
        short-signal (cross-up (:close ds-bollinger) (:bollinger-lower ds-bollinger))
        entry (dtype/clone (dtype/emap entry-one :keyword long-signal short-signal))
        ds-signal (tc/add-column ds-bollinger :entry entry entry)]
    ds-signal))

(defn bollinger-stats [opts ds-d ds-m]
  (let [day-mid (-> ds-d :bollinger-mid last)
        min-mid (-> ds-m :bollinger-mid last)]
    {:day-dt (-> ds-d :date last)
     :day-mid day-mid
     :min-dt (-> ds-m :date last)
     :min-mid min-mid
     :diff (- min-mid day-mid)}))

(defn get-trailing-bars-log [env opts dt]
  (m/sp
    ;(t/log! (str "get-trailing-bars dt:" dt " opts: " opts)) 
   (let [bar-ds (m/? (get-trailing-bars env opts dt))]
      ;(t/log! (str "get-trailing-bars-ds: " bar-ds)) 
     (log env "bar-ds: " bar-ds)
     bar-ds)))

(def bollinger-algo
  {:* {:asset "BTCUSDT"} ; this options are global
   :bars-day {:calendar [:crypto :d]
              :fn get-trailing-bars-log
              :trailing-n 800}
   :day {:formula [:bars-day]
         :fn  bollinger-calc
         :env? true
         :atr-n 10
         :atr-k 0.6}
   :bars-min {:calendar [:crypto :m]
              :fn get-trailing-bars-log
              :trailing-n 20}
   :min {:formula [:bars-min]
         :fn bollinger-calc   ; min gets the global option :asset 
         :env? true
         :trailing-n 20         ; on top of its own local options 
         :atr-n 5
         :atr-k 0.3}
   :stats {:formula [:day :min]
           :fn bollinger-stats
           :carry-n 2}})

(spec->ops bollinger-algo)

(-> bollinger-algo
    (apply-options {[:* :asset] "ETHUSDT"
                    [:bars-day :calendar] [:forex :h]}))
;; => {:* {:asset "ETHUSDT"},
;;     :bars-day {:calendar [:forex :h], :fn #function[dev.algo-bollinger/get-trailing-bars-log], :trailing-n 800},
;;     :day {:formula [:bars-day], :fn #function[dev.algo-bollinger/bollinger-calc], :env? true, :atr-n 10, :atr-k 0.6},
;;     :bars-min {:calendar [:crypto :m], :fn #function[dev.algo-bollinger/get-trailing-bars-log], :trailing-n 20},
;;     :min
;;     {:formula [:bars-min],
;;      :fn #function[dev.algo-bollinger/bollinger-calc],
;;      :env? true,
;;      :trailing-n 20,
;;      :atr-n 5,
;;      :atr-k 0.3},
;;     :stats {:formula [:day :min], :fn #function[dev.algo-bollinger/bollinger-stats], :carry-n 2}}


