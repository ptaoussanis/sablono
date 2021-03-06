(ns sablono.test
  (:refer-clojure :exclude [replace])
  (:require [clojure.string :refer [replace]]
            [sablono.core :as core]))

(defmacro are-html-rendered [& body]
  `(cemerick.cljs.test/are [form# expected#]
     (cemerick.cljs.test/is (= expected# (sablono.test/render-dom (sablono.core/html form#))))
     ~@body))

(defmacro html-str [& contents]
  `(sablono.test/render-dom (sablono.core/html ~@contents)))

#+cljs
(defn body []
  (aget (goog.dom/getElementsByTagNameAndClass "body") 0))

(defn strip-react-attrs
  "Strip the React attributes from `s`."
  [s] (replace (str s) #"\s+data-reactid=\"[^\"]+\"" ""))

#+cljs
(defn render-dom [children]
  (let [container (goog.dom/createDom "div")
        id (gensym)]
    (goog.dom/append (body) container)
    (let [render-fn (fn [] (this-as this (js/React.DOM.div (clj->js {:id id}) children)))
          component (js/React.createClass #js {:render render-fn})]
      (js/React.renderComponent (component) container)
      (let [html (.-innerHTML (goog.dom/getElement (str id)))]
        (goog.dom/removeNode container)
        (sablono.test/strip-react-attrs html)))))
