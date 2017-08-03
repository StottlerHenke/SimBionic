package com.stottlerhenke.simbionic.test.parameterpassing;

public class MyModel {

	protected  int count;
	
	public MyModel() {
		count = 0;
	}

	public String getOne() {
		return "Foo";
	}

	public MyEnum getTwo() {
		return MyEnum.TWO;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void incrementCount() {
		count += 1;
	}
}
