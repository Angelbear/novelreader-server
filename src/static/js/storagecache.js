LRUStorageCache = function(max_size) {
	var API = { constructor: LRUStorageCache };
	var p = API.properties = {
		items : {},
		maxSize : max_size ||  1024 * 1024 * 2.5
	};

	function initialize() {
		if (localStorage != null && localStorage['LRUStorageCache'] != null) {
			p.items = JSON.parse(localStorage['LRUStorageCache']);
		}
	}

	function getItem(key) {
		var item = p.items[key];
		if (item != null) {
			item.lastAccessed = new Date().getTime();
			_sync();
		} else {
			return null;
		}
		return item.value;
	}

	function setItem(key, value, options) {
		function CacheItem(k, v) {
			if ( k == null || k == '') {
				throw new Error('key cannot be null or empty');
			}
			this.key = k;
			this.value = v;
			this.lastAccessed = new Date().getTime();
		}

		var valueSize = JSON.stringify(value).length;
		if (valueSize > p.maxSize) {
			throw new Error('value can not be too big');
		}

		if (p.items[key] != null) {
			delete p.items[key];
		}

		_purgeForSize(valueSize - leftSpace());
		p.items[key] = new CacheItem(key, value);
		_sync();
	}

	function deleteItem(key) {
		if (p.items[key] != null) {
			delete p.items[key];
			_sync();
		}
	}

	function clear() {
		p.items = {};
		_sync();
	}

	function leftSpace() {
		return p.maxSize - unescape(encodeURIComponent(JSON.stringify(p.items))).length;
	}

	function _sync() {
		if (localStorage != null) {
			localStorage['LRUStorageCache'] = JSON.stringify(p.items);
		}
	}

	function _purgeForSize(purgeSize) {
		if (purgeSize > 0) {
			var tmpArray = new Array();
			for (var key in p.items) {
				tmpArray.push(p.items[key]);
			}
			tmpArray = tmpArray.sort(function(a,b) {
				return b.lastAccessed - a.lastAccessed;
			});
			var delete_size = 0;
			while (delete_size < purgeSize) {
				var item_delete = tmpArray.pop();
				delete_size += unescape(encodeURIComponent(JSON.stringify(item_delete))).length;
				delete p.items[item_delete.key];
			}
		}
	}


	API.getItem = getItem;
	API.setItem = setItem;
	API.deleteItem = deleteItem;
	API.clear = clear;

	initialize();
	return API;
};
