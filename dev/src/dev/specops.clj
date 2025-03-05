(ns dev.specops
  (:require
   [quanta.algo.spec :as s]
   [quanta.algo.options :refer [apply-options]]))

(defn bongo [& _] nil)

(def bollinger-algo
  {:* {:asset "BTCUSDT"} ; this options are global
   :dt-day {:fn bongo
            :calendar [:crypto :d]}
   :bars-day {:fn bongo
              :deps [:dt-day]
              :trailing-n 800}
   :day {:fn bongo
         :deps [:bars-day]
         :env? true
         :atr-n 10
         :atr-k 0.6}
   :dt-min {:fn bongo
            :calendar [:crypto :d]}
   :bars-min {:fn bongo
              :deps [:dt-min]
              :trailing-n 20}
   :min {:fn bongo   ; min gets the global option :asset 
         :deps [:bars-min]
         :env? true
         :trailing-n 20         ; on top of its own local options 
         :atr-n 5
         :atr-k 0.3}
   :stats {:fn bongo
           :deps [:day :min]
           :carry-n 2}})

(s/spec->ops bollinger-algo)

