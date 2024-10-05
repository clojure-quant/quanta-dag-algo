(ns dev.dag
  (:require
   [missionary.core :as m]
   [tick.core :as t]
   [quanta.dag.core :as dag]
   [quanta.dag.util :as util2]))

(def model
  (-> (dag/create-dag {:log-dir ".data/"})
      (dag/add-constant-cell :a 2)
      (dag/add-constant-cell :b 3)
      (dag/add-constant-cell :c 5)
      (dag/add-formula-cell :d + [:a :b])
      (dag/add-formula-cell :e * [:c :d])
      (dag/add-formula-cell :f * [:e :d :a :b])))

(dag/get-current-value model :a)
(dag/get-current-value model :b)
(dag/get-current-value model :d)
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
      (dag/add-formula-cell :assets (fn [asset1 asset2]
                                      [asset1 asset2]) [:asset :asset2])
      (dag/add-cell :dt dt-every-10-seconds)
      (dag/add-formula-cell :quote (fn [asset dt]
                                     {:asset asset
                                      :dt dt
                                      :price (rand 100)}) [:asset :dt])))

(dag/cell-ids dag-rt)

(dag/get-current-value dag-rt :asset)
(dag/get-current-value dag-rt :assets)
(dag/get-current-value dag-rt :dt)

(dag/get-current-valid-value dag-rt :dt)
(dag/get-current-valid-value dag-rt :quote)

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



