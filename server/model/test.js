var Entity = require("./Entity.js");






function test(){
	this.type = "Test";
	this.$ = {};
}

test.prototype = Entity;






var n = new test();


n.test();