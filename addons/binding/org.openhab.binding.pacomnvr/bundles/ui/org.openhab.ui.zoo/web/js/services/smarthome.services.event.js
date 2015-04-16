// Generated by CoffeeScript 1.9.1
'use strict';
angular.module('SmartHome.services.event', []).factory('eventService', function() {
  var EventService, eventSrc;
  eventSrc = new EventSource('/rest/events');
  return new (EventService = (function() {
    function EventService() {}

    EventService.prototype.createRegexFromTopic = function(topic) {
      return topic.replace('/', '\/').replace('*', '.*');
    };

    EventService.prototype.onEvent = function(topic, callback) {
      var topicRegex;
      topicRegex = this.createRegexFromTopic(topic);
      return eventSrc.addEventListener('message', function(event) {
        var data;
        data = JSON.parse(event.data);
        if (data.topic.match(topicRegex)) {
          return callback(data.topic, data.object);
        }
      });
    };

    return EventService;

  })());
});
