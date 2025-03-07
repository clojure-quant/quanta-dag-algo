(ns dev.algo.children.algo
  (:require
   ; envs
   [quanta.calendar.env :refer [get-calendar]]
   ; algo dag
   [quanta.algo.options :refer [apply-options]]
   [quanta.algo.spec :refer [spec->ops]]))

(defn demo-calc [opts dt]
  {:dt dt
   :asset (:asset opts)
   :msg "demo-algo"})

(defn demo-overview [opts dt & children]
  {:msg "overview"
   :dt dt
   :assets children})

(def simple-algo-children
  {:* {:x 3}
   :dt {:fn get-calendar
        :calendar [:crypto :m]}
   :demo {:> {:asset (range 10)}
          :fn demo-calc
          :deps [:dt]
          :env? false
          :demo-opt-1 42}
   :overview {:fn demo-overview
              :deps [:dt] ;:demo
              :env? false}})

(spec->ops simple-algo-children)
;; => ([:algo {:x 3, :calendar [:crypto :m], :fn #function[dev.algo-simple/simple-calc], :env? false, :y :b, :z nil}])