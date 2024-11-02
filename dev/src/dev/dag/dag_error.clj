(ns dev.dag.dag-error
  (:require
   [quanta.dag.core :as dag]))

(defn bad-fn [a b]
  (throw (ex-info "something bad happend" {:a a
                                           :b b})))

(def model
  (-> (dag/create-dag {:log-dir ".data/"})
      (dag/add-constant-cell :a 2)
      (dag/add-constant-cell :b 3)
      (dag/add-constant-cell :c 5)
      (dag/add-formula-cell :d + [:a :b] false)
      (dag/add-formula-cell :e * [:c :d]  false)
      (dag/add-formula-cell :f * [:e :d :a :b]  false)
      (dag/add-formula-cell :g bad-fn [:c :e]  false)))

(dag/get-current-value model :a)
(dag/get-current-value model :b)
(dag/get-current-value model :d)
(dag/get-current-value model :e)
(dag/get-current-value model :f)
;(dag/get-current-value model :g)

;(get-value-safe model :g)
;; => 




