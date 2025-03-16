(ns dev.algo.simple.algo
  (:require
   [quanta.calendar.interval :as i]
   ; envs
   [quanta.calendar.env :refer [get-calendar]]
   ; algo dag
   [quanta.algo.options :refer [apply-options]]
   [quanta.algo.spec :refer [spec->ops]]))

(defn demo-calc [opts dt]
  {:msg "demo-calc"
   :opts opts :dt dt})

(def simple-algo
  {:* {:x 3}
   :interval {:fn get-calendar
              :calendar [:crypto :m]
              :env? true}
   :dt {:fn (fn [_ interval]
              (println "calculating interval close date..")
              (-> interval i/current :close))
        :deps [:interval]
        :env? false}
   :demo {:fn demo-calc
          :deps [:dt]
          :env? false
          :opts? true
          :demo-opt-1 42}})

(spec->ops simple-algo)
;; => ([:algo {:x 3, :calendar [:crypto :m], :fn #function[dev.algo-simple/simple-calc], :env? false, :y :b, :z nil}])

(apply-options simple-algo {[:algo :calendar] [:forex :h]})
;; => {:* {:x 2}, :algo {:calendar [:crypto :m], :fn #function[dev.algo-simple/simple-calc], :env? false, :y :b, :z 5}}

