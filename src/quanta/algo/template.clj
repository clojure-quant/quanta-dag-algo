(ns quanta.algo.template
  (:require
   [taoensso.timbre :refer [debug info warn error]]
   [nano-id.core :refer [nano-id]]
   [quanta.dag.core :as dag]
   [quanta.algo.options :as algo-opts]
   [quanta.algo.core :as algo]))

;; TEMPLATE INFO

(defn get-views
  "returns all vizualisation-ids of a template
   in a vector."
  [template]
  (let [views (-> template
                  (dissoc :id :options :algo :md)
                  (keys)
                  sort)]
    (->> views
         (concat [:select-viz])
         (into []))))

(defn- get-default-value [template path]
  (debug "getting default value template: " (:id template) " path: " path)
  (let [algo (:algo template)
        [k v]  (cond
                 (keyword path)
                 [path (get algo path)]

                 (vector? path)
                 [path (get-in algo path)]

                 :else
                 [path nil])]
    ;(info "getting default value algo: " algo " path: " path)
    [k v]))

(defn- get-default-values [template options]
  ;(info "getting default values options: " options)
  (let [paths (map :path options)]
    ;(info "paths: " paths)
    (->> (map #(get-default-value template %) paths)
         (into {}))
    #_(:algo template)))

(defn template-info
  "returns information about a template.
   :views - a vector of vizualisation ids
   :options - a list of options that can be edited in the ui
   :current - the default default options of the algo"
  [template]
  (let [options (or (:options template) [])
        options (if (vector? options)
                  options
                  (options) ; options could be a function, in which case we need to execute it.
                  )]
    {:options options
     :current (get-default-values template options)
     :views (get-views template)}))

;; APPLY OPTIONS

(defn- get-option-by-path [template path]
  (let [options (or (:options template) [])]
    (->> (filter #(or (= (:path %) path)
                      (= [(:path %)] path)) options)
         first)))

(defn- coerce-to
  "returns a keyword if value in path should be coerced"
  [template path]
  (:coerce (get-option-by-path template path)))

(defn- coerce-value
  "returns a keyword if value in path should be coerced"
  [template path v]
  (if-let [c (coerce-to template path)]
    (do (info "coercing value in path: " path " coercer: " c " v: " v)
        [path (case c
                :int (parse-long (str v))
                :double (parse-double (str v))
                v)])
    [path v]))

(defn- coerce-options [template options]
  (->> options
       (map (fn [[path v]]
              (coerce-value template path v)))
       (into {})))

(defn apply-options
  "sets options for a template. 
   returns a variation of the template"
  ([template options]
   ; this path is used by bruteforcer
   (apply-options template options false))
  ([template options coerce-enabled?]
  ; if all paths are keys, this is really simple.
  ; (update template :algo merge options)
  ; but if we can have hierarchical paths, then we 
  ; need to set them via specter, so type gets
  ; preserved. 
   (try (let [options (if coerce-enabled?
                        (coerce-options template options)
                        options)]
          (assoc template :algo (algo-opts/apply-options (:algo template) options)))
        (catch Exception ex
          (error "template:apply-options"
                 {:template template
                  :options options}
                 ex)
          (throw (ex-info "options-apply-ex" {:options options}))))))

;; CALCULATE

(defn add-viz-cell
  "adds cell-id :viz to the dag
   which contains the result of viz-fn"
  [d template viz-mode]
  (info "adding viz " viz-mode  " to dag as cell :viz ..")
  (let [mode (get template viz-mode)
        _ (assert mode (str "viz key " viz-mode " not found."))
        {:keys [viz viz-options key]
         :or {key :algo}} mode
        formula-fn (partial viz viz-options)]
    (assert (dag/get-cell d key) (str "dag does not contain viz cell: " key))
    (info "adding viz-cell... ")
    (dag/add-formula-cell d :viz {:fn formula-fn
                                  :input [key]})))

(defn calculate
  "this runs a viz-task once and returns the viz-result.
   output is guaranteed to be always viz-spec format, so
   possible anomalies are converted to viz-spec.
   dag-env {:env :log-dir}
   template {:algo :viz-as-per-viz-mode}
   viz-mode: the vizualisation that should be returned
   dt: the as-of-date-time"
  [dag-env template viz-mode dt]
  (info "creating algo-dag..")
  (let [algo (:algo template)
        d (-> (dag/create-dag dag-env #_{:log-dir ".data/" :env env})
              (algo/add-env-time-snapshot dt)
              (algo/add-algo algo))]
    (add-viz-cell d template viz-mode)
    (info "waiting for viz result.. ")
    (dag/get-current-valid-value d :viz)))