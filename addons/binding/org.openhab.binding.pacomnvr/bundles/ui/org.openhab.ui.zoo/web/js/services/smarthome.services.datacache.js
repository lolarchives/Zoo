// Generated by CoffeeScript 1.9.1
'use strict';
angular.module('SmartHome.services.datacache', []).factory('DataCache', [
  '$q', function($q) {
    var DataCache, cache;
    cache = {};
    return new (DataCache = (function() {
      function DataCache() {}

      DataCache.cacheEnabled = false;

      DataCache.dirty = false;

      DataCache.prototype.init = function(remoteService) {
        this.remoteService = remoteService;
        return this;
      };

      DataCache.prototype.getAll = function(refresh) {
        var deferred;
        deferred = $q.defer();
        if (this.dirty || !this.cacheEnabled || refresh || data.length !== (cache != null ? cache.length : void 0)) {
          this.remoteService.getAll(function(data) {
            cache = angular.copy(data);
            return deferred.resolve(data);
          });
        } else {
          deferred.resolve(cache);
        }
        return deferred.promise;
      };

      DataCache.prototype.getOne = function(condition, callback, refresh) {
        var element, onError, onSuccess;
        element = this.find(condition);
        if (element && !this.dirty && !refresh) {
          return callback(element);
        } else {
          onSuccess = (function(_this) {
            return function(res) {
              return typeof callback === "function" ? callback(_this.find(condition)) : void 0;
            };
          })(this);
          onError = function() {
            return typeof callback === "function" ? callback(null) : void 0;
          };
          return this.getAll(null, true).then(onSuccess, onError);
        }
      };

      DataCache.prototype.find = function(condition) {
        var element, i, len;
        for (i = 0, len = cache.length; i < len; i++) {
          element = cache[i];
          if (condition(element)) {
            return element;
          }
        }
      };

      DataCache.prototype.add = function(element) {
        return cache.push(element);
      };

      DataCache.prototype.remove = function(element) {
        var i, idx, len, results;
        results = [];
        for (idx = i = 0, len = cache.length; i < len; idx = ++i) {
          element = cache[idx];
          if (condition(element)) {
            results.push(delete cache[idx]);
          }
        }
        return results;
      };

      DataCache.prototype.setDirty = function(dirty) {
        this.dirty = dirty;
      };

      return DataCache;

    })());
  }
]);
