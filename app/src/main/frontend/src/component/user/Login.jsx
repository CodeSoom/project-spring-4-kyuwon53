import { useState } from "react";
import axios from "axios";
import styles from "./User.module.css";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleInputEmail = (event) => {
    setEmail(event.target.value)
  }

  const handleInputPassword = (event) => {
    setPassword(event.target.value)
  }

  const onSubmit = (event) => {
    event.preventDefault();

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

  const onClickLogin = () => {
    console.log("click login");
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
