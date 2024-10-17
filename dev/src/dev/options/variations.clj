(ns dev.options.variations
  (:require
   [quanta.algo.options :as o]))

(o/make-variations [:x [1 2 3]
                    :y [:a :b]])
  ;; => ({:x 1, :y :a}
  ;;     {:x 1, :y :b} 
  ;;     {:x 2, :y :a} 
  ;;     {:x 2, :y :b} 
  ;;     {:x 3, :y :a} 
  ;;     {:x 3, :y :b})

  ;; test new map syntax; this is more convenient

(o/make-variations {:x [1 2 3]
                    :y [:a :b]})
  ;; => ({:x 1, :y :a} {:x 1, :y :b} {:x 2, :y :a} {:x 2, :y :b} {:x 3, :y :a} {:x 3, :y :b})

(partition 2 [:a 1 :b 2 :c 3])
(map concat {:a 1 :b 2 :c 3})

(o/make-variations [:x [1 2 3]
                    :y [:a :b :c]
                    :debug [true false]])
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
    ;;     {:x 3, :y :c, :debug false})

(def algo {:a 1
           :dt :now
           :z 12})

(o/create-algo-variations
 algo
 {:z [1 2 3]})
  ;; => ({:a 1, :dt :now, :z 1}
  ;;     {:a 1, :dt :now, :z 2} 
  ;;     {:a 1, :dt :now, :z 3})

(def algo2 {:a 1
            :day {:dt :now}
            :z 12})

(o/create-algo-variations
 algo2
 {[:day :dt] [:yesterday :today]})
  ;; => ({:a 1, :day {:dt :yesterday}, :z 12}
  ;;     {:a 1, :day {:dt :today}, :z 12})

(def bollinger-algo
  [{:asset "BTCUSDT"} ; this options are global
   :day {:calendar [:crypto :d]
         :algo  :bollinger-calc-fn
         :trailing-n 800
         :atr-n 10
         :atr-k 0.6}])

(o/create-algo-variations
 bollinger-algo
 {[2 :atr-n] [5 10]})
;; => ([{:asset "BTCUSDT"} :day {:calendar [:crypto :d], :algo :bollinger-calc-fn, :trailing-n 800, :atr-n 5, :atr-k 0.6}]
;;     [{:asset "BTCUSDT"} :day {:calendar [:crypto :d], :algo :bollinger-calc-fn, :trailing-n 800, :atr-n 10, :atr-k 0.6}])

