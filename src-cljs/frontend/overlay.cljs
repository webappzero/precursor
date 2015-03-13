(ns frontend.overlay
  (:require [frontend.state :as state]))

(defn clear-overlays [state]
  (assoc-in state state/overlays-path []))

(defn add-overlay [state overlay]
  (update-in state state/overlays-path conj overlay))

(defn safe-pop [v]
  (if (empty? v)
    v
    (pop v)))

(defn pop-overlay [state]
  (update-in state state/overlays-path safe-pop))

(defn replace-overlay [state overlay]
  (assoc-in state state/overlays-path [overlay]))

(defn current-overlay [state]
  (last (get-in state state/overlays-path)))

(defn overlay-visible? [state]
  (last (get-in state state/overlays-path)))

(defn overlay-count [state]
  (count (get-in state state/overlays-path)))

(def team-overlays #{:roster :team-settings :team-doc-viewer})

(defn app-overlay-class [state]
  (when (overlay-visible? state)
    (str " state-menu "
         (if (contains? team-overlays (current-overlay state))
           " state-menu-right "
           " state-menu-left "))))
