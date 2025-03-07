(ns quanta.algo.core
  (:require
   [missionary.core :as m]
   [quanta.dag.core :as dag]
   [quanta.dag.trace :refer [write-edn-raw]]
   [quanta.algo.spec :refer [spec->ops]]
   [quanta.algo.options :refer [apply-options create-algo-variations]]))

(defn mix
  "Return a flow which is mixed by flows"
  ; will generate (count flows) processes, 
  ; so each mixed flow has its own process
  [& flows]
  (m/ap (m/?> (m/?> (count flows) (m/seed flows)))))

(defn add-children-summary [d cell-id child-ids]
  (let [child-flows (map (fn [child-id]
                           (let [cell-id [cell-id child-id]
                                 _ (println "get-child-flow: " cell-id)
                                 cell-f (dag/get-cell d cell-id)]
                             (m/ap
                              {:child-id child-id
                               :child-val (m/?> cell-f)}))) child-ids)
        _ (println "child-flows " child-flows)
        child-data-f (apply mix child-flows)
        state (atom {})
        summary-f (m/ap
                   (let [new-data (m/?> child-data-f)]
                     (swap! state assoc (:child-id new-data) (:child-val new-data))))]
    (dag/add-cell d cell-id summary-f)))

(declare add-cell)
(defn add-children [d cell-id {:keys [template variations]}]
  ; template
  ;[:formula
  ; {:input [:dt],
  ;  :fn #function[dev.algo.children.algo/demo-calc],
  ;  :sp? false,
  ;  :env? false,
  ;  :opts {:x 3, :demo-opt-1 42}}]
  (let [[cell-type cell-opts] template
        algos (create-algo-variations (:opts cell-opts) variations)
        variation-keys (keys variations)
        _ (println "template: " template)
        child-ids (map (fn [v]
                         (let [id (select-keys v variation-keys)]
                           id)) algos)
        create-child (fn [v]
                       (let [id (select-keys v variation-keys)
                             algo-id [cell-id id]
                             cell-opts (assoc cell-opts :opts v)]
                         (println "create-child id: " algo-id "v :" cell-opts)
                         (add-cell d [algo-id cell-type cell-opts])))]
    (println "child-ids: " child-ids)
    (doall
     (map create-child algos))
    ; summary flow
    (add-children-summary d cell-id child-ids)))

(defn- add-cell [d [cell-id cell-type cell-opts]]
  (try
    (case cell-type
      :children
      (add-children d cell-id cell-opts)

      :flow
      (dag/add-flow-cell d cell-id cell-opts)

      :formula
      (dag/add-formula-cell d cell-id cell-opts)

      :formula-raw
      (dag/add-formula-raw-cell d cell-id cell-opts)
      (throw (ex-info (str "unknown cell-type " cell-type) {:cell-id cell-id
                                                            :cell-type cell-type
                                                            :cell-opts cell-opts})))
    (catch Exception ex
      (throw (ex-info (str "create-cell " cell-id) {:message (ex-message ex)
                                                    :ex ex
                                                    :cell-id cell-id
                                                    :cell-type cell-type
                                                    :cell-opts cell-opts})))))

(defn- add-cells [d cell-spec]
  (doall (map #(add-cell d %) cell-spec)))

(defn add-algo [dag algo-spec]
  (let [cell-spec (spec->ops algo-spec)]
    (write-edn-raw (:logger dag) "\r\nadding-algo" cell-spec)
    (add-cells dag cell-spec)
    dag))




