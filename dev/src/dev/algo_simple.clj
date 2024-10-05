(ns dev.algo-simple
  (:require
   [quanta.algo.options :refer [apply-options]]
   [quanta.algo.dag.spec :refer [spec->ops]]))

(defn simple-calc [opts dt]
  {:result dt
   :opts opts})

(def simple-algo
  {:calendar [:crypto :m]
   :algo  simple-calc
   :x 3
   :y :b
   :z nil})

(spec->ops simple-algo)

(apply-options simple-algo {[:x] 2
                            [:z] 5})
;; => {:calendar [:forex :m], 
;;     :algo #function[dev.simple-algo/calc-simple], 
;;     :x 2, 
;;     :y :b, 
;;     :z 5}
