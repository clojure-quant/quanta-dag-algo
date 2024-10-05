(ns quanta.algo.dag.spec)

(defn spec->op [spec]
  (let [{:keys [calendar formula value]} spec]
    (cond
      ; time-algo
      calendar
      {:calendar calendar
       :algo-fn (:algo spec)
       :opts (dissoc spec :algo)}
      ; formula (uses other cells)
      formula
      {:formula formula
       :algo-fn (:algo spec)
       :opts (dissoc spec :algo)}
      ; value (create imput cell)
      value
      {:value value}
      ; bad spec syntax
      :else
      (throw (ex-info "unsupported cell-type" {:spec spec})))))

(defn spec->ops
  "returns ops or throws"
  [spec]
  (if (map? spec)
    ; convert map syntax to vector syntax
    (spec->ops [:algo spec])
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

