import { useState } from "react";
import axios from "axios";
import styles from "./User.module.css";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [emailError, setEmailError] = useState(false);
  const [passwordError, setPasswordError] = useState(false);

  const handleInputEmail = (event) => {
    const emailRegex = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/;
    const emailValue = event.currentTarget.value;
    const emailValidation = (!emailValue || (emailRegex.test(emailValue)));

    setEmailError(!emailValidation);
    setEmail(emailValue)
  }

  const handleInputPassword = (event) => {
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,20}$/;
    const passwordValue = event.currentTarget.value;
    const passwordValidation = (!passwordValue || (passwordRegex.test(passwordValue)));

    setPasswordError(!passwordValidation);
    setPassword(passwordValue)
  }

  const onSubmit = (event) => {
    event.preventDefault();

    if (validation()) {
      return
    }

    loginUser();
  }

  async function loginUser() {

    let body = {
      email: email,
      password: password
    }

    const response = await axios.post(
      `http://localhost:8080/session`,
      body
    ).catch((error) => {
      console.log(error);
    })
    console.log(response.data);
    return response.data;
  }

  const validation = () => {
    if (!email) {
      setEmailError(true);
    }
    if (!password) {
      setPasswordError(true);
    }
  }

  return (
    <div className={styles.container}>
      <h2>Login</h2>
      <form>
        <div className={styles.row}>
          <div className={styles.inputGroup}>
            <label htmlFor="email">ID : </label>
            <input
              name="email"
              type="email"
              value={email}
              placeholder="이메일을 입력하세요"
              onChange={handleInputEmail}
            />
            {(email === "") && <div className={styles.invalid}>이메일을 입력하세요. </div>}
            {emailError && <div className={styles.invalid}>이메일 형식에 맞지 않습니다.</div>}
          </div>
          <div className={styles.inputGroup}>
            <label htmlFor="password">PASSWORD : </label>
            <input
              name="password"
              type="password"
              value={password}
              placeholder="비밀번호를 입력하세요"
              onChange={handleInputPassword}
            />
            {(password === "") && <div className={styles.invalid}>비밀번호를 입력하세요. </div>}
            {passwordError && <div className={styles.invalid}>비밀번호 형식에 맞지 않습니다.</div>}
          </div>
          <div>
            <button
              className={styles.actionButton}
              type="submit"
              onClick={onSubmit} >
              Login
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}

export default Login;
