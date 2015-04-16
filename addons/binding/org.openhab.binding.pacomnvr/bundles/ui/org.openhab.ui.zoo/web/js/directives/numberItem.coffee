'use strict'

angular.module('ZooLib.directives.numberItem', []).directive 'numberItem', ->

	restrict: 'E'
	replace: yes
	#templateUrl: 'partials/directives/numberItem.html'
	template: '<b>To be implemented!</b>'
	scope:
		item: '='
	link: (scope, elem, attrs) ->

		scope.options = {}

		for tag in scope.item.tags
			if tag is 'power' then scope.options.cssIconClass = 'i-power'
			if tag is 'light' then scope.options.cssIconClass = 'i-light-on-small'

		return
