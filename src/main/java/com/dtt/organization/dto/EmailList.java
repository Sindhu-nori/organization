package com.dtt.organization.dto;

import java.util.List;

public class EmailList {

	private List<String> emailList;

	public List<String> getEmailList() {
		return emailList;
	}

	public void setEmailList(List<String> emailList) {
		this.emailList = emailList;
	}

	@Override
	public String toString() {
		return "EmailList [emailList=" + emailList + "]";
	}

	
}
