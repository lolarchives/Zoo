'use strict'

angular.module('ZooLib.directives.playerItem', []).directive 'playerItem', ->

	restrict: 'E'
	replace: yes
	#templateUrl: 'partials/directives/playerItem.html'
	template: '<b>To be implemented!</b>'
	scope:
		item: '='
	link: (scope, elem, attrs) ->

		scope.options = {}

		for tag in scope.item.tags
			if tag is 'power' then scope.options.cssIconClass = 'i-power'
			if tag is 'light' then scope.options.cssIconClass = 'i-light-on-small'

		return
