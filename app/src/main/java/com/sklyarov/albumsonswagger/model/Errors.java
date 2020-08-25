package com.sklyarov.albumsonswagger.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Errors {
    @SerializedName("errors")
    private ErrorsBean errors;

    public ErrorsBean getErrors() {
        return errors;
    }

    public void setErrors(ErrorsBean errors) {
        this.errors = errors;
    }

    public static class ErrorsBean {
        @SerializedName("email")
        private List<String> email;
        @SerializedName("name")
        private List<String> name;
        @SerializedName("password")
        private List<String> password;

        public List<String> getEmail() {
            return email;
        }

        public void setEmail(List<String> email) {
            this.email = email;
        }

        public List<String> getName() {
            return name;
        }

        public void setName(List<String> name) {
            this.name = name;
        }

        public List<String> getPassword() {
            return password;
        }

        public void setPassword(List<String> password) {
            this.password = password;
        }
    }
}
