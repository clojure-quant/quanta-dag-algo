(ns quanta.algo.dag.calendar.historic
  (:require
   [ta.calendar.combined :refer [combined-event-seq]]))

; firing old events that need to be syncronized with
; all calendars needs a little refactoring,
; before adding the model, so when it is in cell-spec stage
; we need to calculate the calendars, then we
; can can seed the combined event seq, 
; and from that filter the individual calendar events

(defn fire-backtest-events [calendars window]
  (combined-event-seq calendars window))
