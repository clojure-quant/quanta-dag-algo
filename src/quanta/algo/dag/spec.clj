(ns quanta.algo.dag.spec
  (:require
   [quanta.dag.topology :refer [topological-sort]]))

(defn spec->op [{:keys [calendar formula formula-raw value]
                 :as spec}]
  (cond
      ; time-algo / formula (uses other cells)
    (or calendar formula formula-raw)
    spec
      ; value (create imput cell)
    value
    {:value value}
      ; bad spec syntax
    :else
    (throw (ex-info "unsupported cell-type" {:spec spec}))))

(defn spec->ops-old
  "returns ops or throws"
  [spec]
  (if (map? spec)
    ; convert map syntax to vector syntax
    (spec->ops-old [:algo spec])
    ; process vector syntax
    (let [global-opts? (and (odd? (count spec))
                            (map? (first spec)))
          [global-opts spec] (if global-opts?
                               [(first spec) (rest spec)]
                               [{} spec])]
      ;(info "global-opts: " global-opts)
      (->> (reduce (fn [r [id spec]]
                     (let [spec (merge global-opts spec)
                           op (spec->op spec)]
                       ;(info "merged spec: " spec)
                       (conj r [id op])))
                   []
                   (partition 2 spec))
           ;(into [])
           ))))

(defn- get-deps [{:keys [calendar formula formula-raw]}]
  (cond
    calendar
    #{}
    formula
    (into #{} formula)
    formula-raw
    (into #{} formula-raw)
    :else
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
      (map (fn [cell-id]
             (->> (get cells cell-id)
                  (merge global-opts)
                  (spec->op)
                  (conj [cell-id])))
           cell-ids)
      (throw (ex-info "dag has cyclic dependency" cells)))))

