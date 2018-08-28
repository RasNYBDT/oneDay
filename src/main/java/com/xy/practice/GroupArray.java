package com.xy.practice;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GroupArray {

	private static int[] source = {0, 2, 3, 8, 2, 2, 2, 2, 2, 2, 0, 2, 3, 8, 2, 2, 2, 2, 2, 2, 0, 2, 3, 8, 2, 2, 2, 2, 2, 2, 0, 2, 3, 8, 2, 2, 2, 2, 2, 2, 0, 2, 3, 8, 2, 2, 2, 2, 2, 2};
	private static int[] source1 = {0,2,3,8,2,2,2,2,2,2,0};
	public static void main(String[] args) {
		
		int number = 0;
		int cnt = 0;
		List<Map<Integer, Integer>> result = Lists.newArrayList();
		for (int i = 0; i < source1.length; i ++) {
			if (i == 0) {
				number = source[i];
			}
			if (number != source[i]) {
				Map<Integer, Integer> group = Maps.newHashMap();
				group.put(number, cnt);
				result.add(group);
				
				number = source[i];
				cnt = 0;
			}
			cnt ++;
		}
		Map<Integer, Integer> group = Maps.newHashMap();
		group.put(number, cnt);
		result.add(group);
		System.out.println(JSON.toJSONString(result));
	}
}
class Group {
	private int priority;
	private int number;
	private int cnt;
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	
	
}