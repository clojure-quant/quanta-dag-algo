(ns dev.topology
  (:require
   [quanta.algo.dag.spec :refer [spec->ops-old spec->ops]]))

(def bollinger-algo
  [{:asset "BTCUSDT"} ; this options are global
   :bars-day {:calendar [:crypto :d]
              :fn :get-trailing-bars-log
              :trailing-n 800}
   :day {:formula [:bars-day]
         :fn  :bollinger-calc
         :env? true
         :atr-n 10
         :atr-k 0.6}
   :bars-min {:calendar [:crypto :m]
              :fn :get-trailing-bars-log
              :trailing-n 20}
   :min {:formula [:bars-min]
         :fn :bollinger-calc   ; min gets the global option :asset 
         :env? true
         :trailing-n 20         ; on top of its own local options 
         :atr-n 5
         :atr-k 0.3}
   :stats {:formula [:day :min]
           :fn :bollinger-stats
           :carry-n 2}])

(spec->ops-old bollinger-algo)
;; => [[:bars-day {:asset "BTCUSDT", :calendar [:crypto :d], :fn :get-trailing-bars-log, :trailing-n 800}]
;;     [:day {:asset "BTCUSDT", :formula [:bars-day], :fn :bollinger-calc, :env? true, :atr-n 10, :atr-k 0.6}]
;;     [:bars-min {:asset "BTCUSDT", :calendar [:crypto :m], :fn :get-trailing-bars-log, :trailing-n 20}]
;;     [:min {:asset "BTCUSDT", :formula [:bars-min], :fn :bollinger-calc, :env? true, :trailing-n 20, :atr-n 5, :atr-k 0.3}]
;;     [:stats {:asset "BTCUSDT", :formula [:day :min], :fn :bollinger-stats, :carry-n 2}]]

(def bollinger-algo-new
  {:* {:asset "BTCUSDT"} ; this options are global
   :bars-day {:calendar [:crypto :d]
              :fn :get-trailing-bars-log
              :trailing-n 800}
   :day {:formula [:bars-day]
         :fn  :bollinger-calc
         :env? true
         :atr-n 10
         :atr-k 0.6}
   :bars-min {:calendar [:crypto :m]
              :fn :get-trailing-bars-log
              :trailing-n 20}
   :min {:formula [:bars-min]
         :fn :bollinger-calc   ; min gets the global option :asset 
         :env? true
         :trailing-n 20         ; on top of its own local options 
         :atr-n 5
         :atr-k 0.3}
   :stats {:formula [:day :min]
           :fn :bollinger-stats
           :carry-n 2}})

(spec->ops bollinger-algo-new)
;; => ([:bars-day {:asset "BTCUSDT", :calendar [:crypto :d], :fn :get-trailing-bars-log, :trailing-n 800}]
;;     [:bars-min {:asset "BTCUSDT", :calendar [:crypto :m], :fn :get-trailing-bars-log, :trailing-n 20}]
;;     [:day {:asset "BTCUSDT", :formula [:bars-day], :fn :bollinger-calc, :env? true, :atr-n 10, :atr-k 0.6}]
;;     [:min {:asset "BTCUSDT", :formula [:bars-min], :fn :bollinger-calc, :env? true, :trailing-n 20, :atr-n 5, :atr-k 0.3}]
;;     [:stats {:asset "BTCUSDT", :formula [:day :min], :fn :bollinger-stats, :carry-n 2}])

