(ns frontend.components.app
  (:require [cemerick.url :as url]
            [cljs.core.async :as async :refer [>! <! alts! chan sliding-buffer close!]]
            [clojure.set :as set]
            [clojure.string :as str]
            [frontend.analytics :as analytics]
            [frontend.async :refer [put!]]
            [frontend.auth :as auth]
            [frontend.components.chat :as chat]
            [frontend.components.inspector :as inspector]
            [frontend.components.hud :as hud]
            [frontend.components.key-queue :as keyq]
            [frontend.components.canvas :as canvas]
            [frontend.components.common :as common]
            [frontend.components.landing :as landing]
            [frontend.components.overlay :as overlay]
            [frontend.favicon :as favicon]
            [frontend.overlay :refer [overlay-visible?]]
            [frontend.state :as state]
            [frontend.utils :as utils :include-macros true]
            [frontend.utils.seq :refer [dissoc-in select-in]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [ankha.core :as ankha])
  (:require-macros [frontend.utils :refer [html]]))

(def keymap
  (atom nil))

(defn app* [app owner]
  (reify
    om/IDisplayName (display-name [_] "App")
    om/IRender
    (render [_]
      (let [{:keys [cast! handlers]} (om/get-shared owner)
            chat-opened? (get-in app state/chat-opened-path)
            right-click-learned? (get-in app state/right-click-learned-path)]
        (html [:div.app-main {:on-click (when (overlay-visible? app)
                                          #(cast! :overlay-closed))}
               [:div.canvas-background]
               [:div.app-canvas {:onContextMenu (fn [e]
                                                 (.preventDefault e)
                                                 (.stopPropagation e))}
                (om/build canvas/svg-canvas app)
                (om/build hud/hud app)]
               (om/build chat/chat app)])))))

(defn app [app owner]
  (reify
    om/IRender
    (render [_]
      (if (:navigation-point app)
        (dom/div #js {:id "app" :className "app"}

          ;; Pseudo-homepage and outer
          (when (:show-landing? app)
            (om/build landing/landing app))

          ;; Main menu and settings
          (when (overlay-visible? app)
            (om/build overlay/overlay app))

          ;; Canvas and chat
          (om/build app* app)

          ; ;; Hack to give app depth when out of focus
          ; (dom/div #js {:className "app-main-outline"})

          ;; Hack to keep menu button above menu itself
          (om/build overlay/main-menu-button (select-in app [state/overlays-path
                                                             state/main-menu-learned-path])))

        (html [:div#app])))))
