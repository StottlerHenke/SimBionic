c = ": " //If this is b, an error will be generated

function Say(msg) {

	a = "Say" + c;
	print(a + msg);
	com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(msg);
}


