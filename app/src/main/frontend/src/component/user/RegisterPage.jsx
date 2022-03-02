import React, { useState } from "react";
import axios from "axios";
import styles from "./User.module.css";

function RegisterPage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("")
  const [picture, setPicture] = useState("")

  const [nameError, setNameError] = useState(false);
  const [emailError, setEmailError] = useState(false);
  const [passwordError, setPasswordError] = useState(false);
  const [confirmPasswordError, setConfirmPasswordError] = useState(false);

  const onNameHandler = (event) => {
    const nameRegex = /^[가-힣a-zA-Z]{2,}$/;
    const nameValue = event.currentTarget.value;
    const nameValidation = (!nameValue || (nameRegex.test(nameValue)));
    if (!nameValidation) {
      event.currentTarget.focus();
    }
    setNameError(!nameValidation);
    setName(nameValue);
  }
  const onEmailHandler = (event) => {
    const emailRegex = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/;
    const emailValue = event.currentTarget.value;
    const emailValidation = (!emailValue || (emailRegex.test(emailValue)));

    setEmailError(!emailValidation);
    setEmail(emailValue);
  }
  const onPasswordHandler = (event) => {
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,20}$/;
    const passwordValue = event.currentTarget.value;
    const passwordValidation = (!passwordValue || (passwordRegex.test(passwordValue)));
    const confirmPasswordValidation = (!confirmPassword || passwordValue === confirmPassword);

    setPasswordError(!passwordValidation);
    setConfirmPassword(!confirmPasswordValidation)
    setPassword(event.currentTarget.value)
  }
  const onConfirmPasswordHandler = (event) => {
    const confirmPasswordValidation = (password === event.currentTarget.value);

    setConfirmPasswordError(!confirmPasswordValidation);
    setConfirmPassword(event.currentTarget.value)
  }
  const onPictureHandler = (event) => {
    setPicture(event.currentTarget.files)
  }
  const validation = () => {
    if (!name) {
      console.log("1")
      setNameError(true);
    }
    if (!password) {
      console.log("2")
      setPasswordError(true);
    }
    if (!confirmPassword) {
      console.log("3")
      setConfirmPasswordError(true);
    }
    if (!email) {
      console.log("4")
      setEmailError(true);
    }
  }

  async function registUser() {
    const response = await axios.post(
      `http://localhost:8080/users`,
      { name, email, password, picture }
    ).catch((error) => {
      console.log(error);
    })
    console.log(response.data);
    return response.data;
  }

  const onSubmit = (event) => {
    event.preventDefault()
    console.log('submit!!');
    if (validation()) {
      console.log("validation오류")
      return
    }
    registUser();
  }

  return (
    <div className={styles.container}>
      <h2>회원가입</h2>
      <form>
        <div className={styles.row}>
          <div className={styles.inputGroup}>
            <input
              name="name"
              type="text"
              placeholder="이름을 입력하세요"
              value={name}
              onChange={onNameHandler}
            />
            {nameError && <div className={styles.invalid}>이름은 2글자 이상입력하세요</div>}
          </div>
          <div className={styles.inputGroup}>
            <input
              name="email"
              type="email"
              placeholder="이메일을 입력하세요"
              value={email}
              onChange={onEmailHandler}
            />
            {emailError && <div className={styles.invalid}>이메일 형식에 맞지 않습니다.</div>}
          </div>
          <div className={styles.inputGroup}>
            <input
              name="password"
              type="password"
              placeholder="비밀번호를 입력하세요"
              value={password}
              onChange={onPasswordHandler}
            />
            {passwordError && <div className={styles.invalid}>비밀번호는 영어,숫자,기호 포함 8-20글자입니다.</div>}
          </div>
          <div className={styles.inputGroup}>
            <input
              name="confirmPassword"
              type="password"
              placeholder="비밀번호 확인"
              value={confirmPassword}
              onChange={onConfirmPasswordHandler}
            />
            {confirmPasswordError && <div className={styles.invalid}>비밀번호가 일치하지 않습니다..</div>}
          </div>
          <div className={styles.inputGroup}>
            <input
              name="picture"
              type="file"
              accept="image/*"
              onChange={onConfirmPasswordHandler}
            />
          </div>
          <button className={styles.actionButton} type="submit" onClick={onSubmit}>가입하기</button>
        </div>
      </form>
    </div>
  );
}

export default RegisterPage;
