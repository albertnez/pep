var app = require('koa')();
var koaBody = require("koa-body");
var router  = require("koa-router")();
var mongo = require('mongodb');
var monk = require('monk');
var co = require("co");

var db = monk('localhost:27017/pepemem');
db = db.get("accounts");

const YEAR = 3;
const MONTH = 2;
const DAY = 1;
const NONE = 0;

const TIME_CHECK=1000*5;

app.use(koaBody());

app.use(function *(next){
	this.db = db;
	yield next;
});


router.post(/\/(.*)\/pay\/([0-9\.]+)/, function*(){
	var account = yield findAccount(this.params[0]);
	if(typeof account == "undefined")
		account = yield createAccount(this.params[0]);
	//console.log(account);
	yield payAccount(account,parseFloat(this.params[1]));

	this.body="Success";

});

router.post(/\/objective/, function*(){

	var pepeaccount = yield findAccount("_pepeAccount");
	if(typeof pepeaccount == "undefined") pepeaccount = yield createAccount("_pepeAccount");


	console.log(this.request.body);

	yield setAccountObjective(pepeaccount,this.request.body);

	yield saveAccount(pepeaccount);
	this.body="Success";

});

router.get(/\/objectives/, function*(){
	var pepeaccount = yield findAccount("_pepeAccount");
	if(typeof pepeaccount == "undefined") pepeaccount = yield createAccount("_pepeAccount");

	this.body = pepeaccount.objectives.pending.concat(pepeaccount.objectives.done);
});

router.get(/\/transactions\/(day|month|year)/, function*(){
	var pepeaccount = yield findAccount("_pepeAccount");
	if(typeof pepeaccount == "undefined") pepeaccount = yield createAccount("_pepeAccount");

	var to = new Date();
	switch(this.params[0]){
	case 'year':
		to = new Date( to.setFullYear(to.getFullYear()-1) );
		break;
	case 'month':
		to = new Date( to.setMonth(to.getMonth()-1) );
		break;
	case 'day':
		to = new Date( to.setDate(to.getDate()-1) );
		break;
	}
	console.log(to);

	this.body = pepeaccount.history.filter(function(el){
		return el.createdAt.valueOf() > to.valueOf();
	});



})
/*
router.get("/", function(){
	this.body = "TOP";
});*/

app.use(router.routes())
	.use(router.allowedMethods());

app.use(require('koa-static')("./static", {}));


app.listen(80);
console.log("Server listening");


function findAccount(name){
	return function(done){
		db.find({"name":name},function(err,data){
				done(null,data[0]);
		});
	}
}

function createAccount(name){
	return function(done){
		db.insert({
			"name":name,
			createdAt: new Date(),
			iban: "ES" + rand(2) + "-"+rand(4) + "-"+rand(4) + "-"+rand(4) + "-"+rand(4) + "-"+rand(4),
			history: [],
			objectives:{
				pending:[],
				done:[]
			},
			money: Math.random()*500
		}, function(err,data){
			done(null,data);
		});
	}
}

function saveAccount(acc){
	return function(done){
		db.update({
			_id: acc._id
		},acc, function(err,data){
			done(null,data);
		});
	}
}

function payAccount(acc,val){
	return function(done){
		co(function*(){

			var pepeaccount = yield findAccount("_pepeAccount");
			if(typeof pepeaccount == "undefined") pepeaccount = yield createAccount("_pepeAccount");

			yield moneyBitches(acc,pepeaccount,-val);
			yield moneyBitches(pepeaccount, acc,val);

			yield saveAccount(pepeaccount);
			yield saveAccount(acc);

		}).then(function(data){done(null,data);}, function(err){console.log(err,err.stack);done(err,null)});
	}
}

function moneyBitches(acc,to,val){
	return function(done){
		acc.money += val;
		acc.history.unshift({
			createdAt: new Date(),
			to: val > 0? acc.iban : to.iban,
			from: val > 0? to.iban:acc.iban,
			val: val,
			total: acc.money
		});
		done(null,null);
	}
}

function setAccountObjective(acc,obj){
	return function(done){
		acc.objectives.pending.push({
			createdAt:new Date(),
			name: obj.name,
			date: new Date(parseInt(obj.date)),
			value: parseFloat(obj.value),
			period: parseInt(obj.period)
		});
		done(null,null);
	}
}

function checkObjectives(){
	co(function*(){
		console.log("Checking objectives");
		var pepeaccount = yield findAccount("_pepeAccount");
		if(typeof pepeaccount == "undefined") pepeaccount = yield createAccount("_pepeAccount");
		var i = 0; 
		while(i < pepeaccount.objectives.pending.length){
			if(checkObjective(pepeaccount,i)) ++i;
		}
		yield saveAccount(pepeaccount);
	});
}

function checkObjective(acc,i){
	var ob = acc.objectives.pending[i];

	console.log("\tchecking " + ob.name);

	if((new Date()).valueOf() > ob.date){
		console.log("Old");
		var ret = true;

		switch(ob.period){
			case YEAR:
				console.log("YEAR");
				ob.date = new Date( ob.date.setFullYear(ob.date.getFullYear()+1) );
				break;
			case MONTH:
				console.log("MONTH");
				ob.date = new Date( ob.date.setMonth(ob.date.getMonth()+1) );
				break;
			case DAY:
				console.log("DAY");
				ob.date = new Date( ob.date.setDate(ob.date.getDate()+1) );
				break;
			case NONE:
				console.log("NONE");
				acc.objectives.done.push(acc.objectives.pending.splice(i,1)[0]);
				ret = false;
		}

		return ret;

	} else if((new Date()).valueOf() + 24*3600*1000 >  ob.date){
		console.log("Objective detected");

	} else console.log("Future")

	return true;
}

var checker = setInterval(checkObjectives,TIME_CHECK);

function rand(n){
	var r = Math.random();
	for(var i = 0; i < n; ++i) r =  r*10;
	return Math.floor(r);
}