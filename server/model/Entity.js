var co = require("co");


module.exports =function(db){ 
	
	function Entity(){
	}

	Entity.prototype.test = function(){
		console.log(this.type);
	}

	Entity.prototype.saveWithCollection = function(collection){
		return function(done){
			co(function*(){

				db[collection].

			}).then(function(data){done(null,data);},function(err){console.log(err,err.stack);done(err)});
		}
	}

	Entity.prototype.save = function(){
		var that = this;
		return function(done){
			co(function*(){

				return yield that.saveWithCollection(that.collection);

			}).then(function(data){done(null,data);},function(err){console.log(err,err.stack);done(err)});
		}

	}
	
	return Entity.prototype;
};