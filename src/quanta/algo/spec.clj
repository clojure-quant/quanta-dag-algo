(ns quanta.algo.spec
  (:require
   [quanta.dag.topology :refer [topological-sort]]))

(defn spec->op [{:keys [fn deps value sp? env? raw?]
                 :as spec}]
  (let [cell-opts (dissoc spec :fn :deps :value :sp? :env? :raw?)]
    (cond
      value
      [:value value]

      (and fn deps)
      (if raw?
        [:formula-raw {:input deps
                       :fn fn
                     ; sp? does not apply here.
                       :env? (if (boolean? env?) env? false) ; default false
                       :opts cell-opts}]
        [:formula {:input deps
                   :fn fn
                   :sp? (if (boolean? sp?) sp? false) ; default false
                   :env? (if (boolean? env?) env? true) ; default true
                   :opts cell-opts}])

      fn
      [:flow {:fn fn
              :opts cell-opts
              :env? (if (boolean? env?) env? true) ; default true
              }]

    ; bad spec syntax
      :else
      (throw (ex-info "unsupported cell-type" {:spec spec})))))

(defn- get-deps [{:keys [deps]}]
  (if deps
    (into #{} deps)
    #{}))

(defn spec->ops
  "takes a algo-cells spec and 
   returns dag-ops 
   or throws"
  [cells]
  (let [global-opts (or (:* cells) {})
        cells (dissoc cells :*)
        cell-ids (->> cells
                      (map (fn [[id m]]
                             [id (get-deps m)]))
                      (into {})
                      (topological-sort))]
    (if cell-ids
      (->> cell-ids
           (map (fn [cell-id]
                  (->> (get cells cell-id)
                       (merge global-opts)
                       (spec->op)
                       (concat [cell-id])
                       (into [])))))
      (throw (ex-info "dag has cyclic dependency" cells)))))

