* 0.7.1-SNAPSHOT - Unreleased
    * Now compatible with Java 11
    * Updated dependencies
        * Clojure 1.10.1
        * pedestal-service 0.5.7
        * liberator 0.15.3
        * ring-core 1.7.1
        * cheshire 5.8.1
        * schema 1.1.11
        * clj-time 0.15.1
        * transit 0.8.313
* 0.7.0 - 2016-02-05
    * Added a `query-param-accept` to help with browser issues
* 0.6.2 - 2015-08-19
    * Fixed a bug how `body-params` interceptor looked for matching
      parsers in the `parser-map`.
* 0.6.1 - 2015-08-07
    * Updated dependencies
        * Clojure 1.6.0 -> 1.7.0
        * liberator 0.11.0 -> 0.13
        * ring 1.3.0 -> 1.4.0
        * cheshire 5.3.1 -> 5.5.0
        * prismatic/schema 0.2.1 -> 0.4.3
        * clj-time 0.7.0 -> 0.10.0
    * Added support for [Transit](https://github.com/cognitect/transit-format) responses
* 0.6.0 - 2015-07-10
    * Upgraded to Pedestal 0.4.0, including some [breaking changes](https://github.com/pedestal/pedestal/releases).
        * Consumers of `pedestal-toolbox.param/body-params` can't pass it into their routes, you must call the fn.
    * Changed the project name from `turbovote.pedestal-toolbox` to `democracyworks/pedestal-toolbox`.
* 0.5.0 - 2014-08-04
    * API change: `turbovote.pedestal-toolbox.content-negotiation/negotiate-content-type` is now `negotiate-response-content-type` to make its purpose clearer
    * `turbovote.pedestal-toolbox.params/body-params` is now an interceptorfn instead of an interceptor; you don't have to change existing route tables, though, because they know how to handle interceptorfns.
    * The new `turbovote.pedestal-toolbox.params/body-params` interceptorfn has a single-arity version that accepts a parser-map of MIME type regexes to parser fns.
        * Ex: `{ #"^application/json" io.pedestal.http.body-params/json-parser }`
        * By default (i.e. the zero-arity version) this function accepts any request bodies that `io.pedestal.http.body-params` can parse.
        * It responds with a 415 Unsupported Media Type if the Content-Type isn't matched by one of the parser-map's keys.
* 0.3.3 - 2014-04-24
    * now allows returning data structures on errors rather than turning them into strings
    * added a JSON encoder for schema.utils.ValidationErrors
    * updated to Clojure 1.6.0 and latest versions of all dependencies
