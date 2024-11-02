(ns dev.algo-bollinger-template
  (:require
   [tick.core :as t]
   [quanta.algo.env.bars]
   [ta.import.provider.bybit.ds :as bybit]
   [dev.algo-bollinger :refer [bollinger-algo]]
   [quanta.algo.template :refer [calculate]]))

(def bar-db (bybit/create-import-bybit))
(def env {#'quanta.algo.env.bars/*bar-db* bar-db})

(defn viz-print [opts data]
  (println "calculating viz-fn with data: " data)
  {:creator "viz-print"
   :data (pr-str data)
   :viz-opts opts})

(def bollinger-template
  {:id :bollinger
   :algo bollinger-algo
   :options [{:type :select
              :path [0 :asset]
              :name "asset"
              :spec ["BTCUSDT" "ETHUSDT"]}
             {:type :string
              :path [2 :atr-n]
              :name "atr-n"
              :coerce :int}]
   :debug {:viz viz-print
           :viz-options {:print-mode :simple}
           :key :bars-day}})

(calculate
 {:log-dir ".data/"
  :env env}
 bollinger-template
 :debug
 (t/instant))


