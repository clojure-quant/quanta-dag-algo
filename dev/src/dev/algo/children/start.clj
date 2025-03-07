(ns dev.algo.children.start
  (:require
   [tick.core :as t]
   [quanta.dag.core :as dag]
   [quanta.algo.core :refer [add-algo]]
   [dev.algo.children.algo :refer [simple-algo-children]]))

;; SNAPSHOT ************************************************************

(def d-once
  (-> (dag/create-dag {:log-dir ".data/"
                       :env {}
                       :opts {:dt (t/instant)}})
      (add-algo simple-algo-children)))

(dag/cell-ids d-once)

(dag/start-log-cell d-once :dt)
(dag/start-log-cell d-once [:demo {:asset 1}])
(dag/start-log-cell d-once [:demo {:asset 3}])
(dag/start-log-cell d-once :demo)

;; REALTIME ************************************************************

(def d
  (-> (dag/create-dag {:log-dir ".data/"
                       :env {}
                       :opts {:dt nil}})
      (add-algo simple-algo-children)))

(dag/start-log-cell d :dt)
(dag/start-log-cell d [:demo {:asset 1}])
(dag/start-log-cell d [:demo {:asset 3}])
(dag/start-log-cell d :demo)
