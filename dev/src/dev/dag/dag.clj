(ns dev.dag.dag
  (:require
   [missionary.core :as m]
   [tick.core :as t]
   [quanta.dag.core :as dag]
   [quanta.dag.util :as util2]))

(def a (atom 12))

(defn mult-task [a b]
  (m/sp
   (println "fast mult!")
   (* a b)))

(defn slow-mult-task [a b]
  (m/sp
   (println "mult-sleep.")
   (m/? (m/sleep 500))
   (println "mult-sleep. done!")
   (* a b)))

(def model
  (-> (dag/create-dag {:log-dir ".data/"})
      (dag/add-constant-cell :a 2)
      (dag/add-constant-cell :b 3)
      (dag/add-atom-cell :c a)
      (dag/add-formula-cell :ab {:fn +
                                 :input [:a :b]
                                 :sp? false})
      (dag/add-formula-cell :ac {:fn mult-task
                                 :input [:c :a]
                                 :sp? true})
      (dag/add-formula-cell :ac2 {:fn slow-mult-task
                                  :input [:c :a]
                                  :sp? true})
      (dag/add-formula-cell :abc {:fn *
                                  :input [:c :ab]
                                  :sp? false})
      (dag/add-formula-cell :ababc {:fn *
                                    :input [:ab :abc]
                                    :sp? false})))

(dag/get-current-value model :a)
(dag/get-current-value model :b)
(dag/get-current-value model :c)
(dag/get-current-value model :ab)
(dag/get-current-value model :ac)

(dag/get-current-value model :abc)
(dag/get-current-value model :ababc)
(dag/get-current-value model :ac2)

(dag/get-cell model :ababc)

(defn current-v
  "gets the first valid value from the flow"
  [f]
  (m/reduce (fn [r v]
              (println "current v: " v " r: " r)
              v) nil
            (m/eduction
            ;(remove dag/is-no-val?)
             (take 1)
             f)))

(-> (m/? (current-v (dag/get-cell model :ac)))
    ;type
    class)

(m/? (current-v (dag/get-cell model :a)))

(dag/get-cell model :e)

(dag/get-current-value model :e)

(dag/get-current-value model :f)

(def dt-every-10-seconds
  (->> (m/ap
        (println "10 sec clock start..")
        (loop [dt (t/instant)]
          (println "sleeping..")
          (m/? (m/sleep 1000))
          (println "sleeping.. done!")
          (m/amb
           dt
           (recur (t/instant)))))
       (m/reductions {} (dag/create-no-val :10-sec))))

; ({} nil 3)
; ({} 3 4)

(def dag-rt
  (-> (dag/create-dag {:log-dir ".data/"})
      (dag/add-constant-cell :asset "QQQ")
      (dag/add-constant-cell :asset2 "QQQ")
      (dag/add-formula-cell :assets {:fn (fn [asset1 asset2]
                                           [asset1 asset2])
                                     :input [:asset :asset2]})
      (dag/add-cell :dt dt-every-10-seconds)
      (dag/add-formula-cell :quote {:fn (fn [asset dt]
                                          {:asset asset
                                           :dt dt
                                           :price (rand 100)})
                                    :input [:asset :dt]})))

(dag/cell-ids dag-rt)

(dag/get-current-value dag-rt :asset)
(dag/get-current-value dag-rt :assets)
(dag/get-current-value dag-rt :dt)

(dag/get-current-value dag-rt :dt)
(dag/get-current-value dag-rt :quote)

(dag/start-log-cell dag-rt :dt)
(dag/start-log-cell dag-rt :quote)

(dag/stop-log-cell dag-rt :dt)
(dag/stop-log-cell dag-rt :quote)

(m/? (->> dt-every-10-seconds
          (m/eduction
           (remove dag/is-no-val?))
          (m/eduction
           (take 2))
          (m/reduce conj)))



