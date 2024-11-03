(ns dev.algo-simple
  (:require
   [quanta.algo.options :refer [apply-options]]
   [quanta.algo.dag.spec :refer [spec->ops]]))

(defn simple-calc [_env opts dt]
  {:result dt
   :opts opts})

(def simple-algo
  {:* {:x 3}
   :algo {:calendar [:crypto :m]
          :fn simple-calc
          :sp? false ; for :calendar :sp? is true by default
          :y :b
          :z nil}})

(spec->ops simple-algo)
;; => ([:algo {:x 3, :calendar [:crypto :m], :fn #function[dev.algo-simple/simple-calc], :env? false, :y :b, :z nil}])

(apply-options simple-algo {[:* :x] 2
                            [:algo :z] 5})
;; => {:* {:x 2}, :algo {:calendar [:crypto :m], :fn #function[dev.algo-simple/simple-calc], :env? false, :y :b, :z 5}}


