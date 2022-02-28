import React, { useState } from "react";
import axios from "axios";

function RegisterPage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("")
  const [picture, setPicture] = useState("")

  const onNameHandler = (event) => {
    setName(event.currentTarget.value)
  }
  const onEmailHandler = (event) => {
    setEmail(event.currentTarget.value)
  }
  const onPasswordHandler = (event) => {
    setPassword(event.currentTarget.value)
  }
  const onConfirmPasswordHandler = (event) => {
    setConfirmPassword(event.currentTarget.value)
  }
  const onPictureHandler = (event) => {
    setPicture(event.currentTarget.files)
  }
  async function registUser() {
    const response = await axios.post(
      `http://localhost:8080/users`,
      { name, email, password, picture }
    ).catch((error)=>{
      console.log(error);
    })
    console.log(response.data);
    return response.data;
  }
  const onSubmit = (event) => {
    event.preventDefault()
    if (password !== confirmPassword) {
      console.log(password, confirmPassword, (password === confirmPassword));
      alert('비밀번호와 비밀번호 확인이 다릅니다.')
      return
    }
    registUser();
  }
  return (
    <div>
      <form>
        <div>
          <input
            name="name"
            type="text"
            placeholder="이름을 입력하세요"
            value={name}
            onChange={onNameHandler}
          />
        </div>
        <div>
          <input
            name="email"
            type="email"
            placeholder="이메일을 입력하세요"
            value={email}
            onChange={onEmailHandler}
          />
        </div>
        <div>
          <input
            name="password"
            type="password"
            placeholder="비밀번호를 입력하세요"
            value={password}
            onChange={onPasswordHandler}
          />
        </div>
        <div>
          <input
            name="confirmPassword"
            type="password"
            placeholder="비밀번호 확인"
            value={confirmPassword}
            onChange={onConfirmPasswordHandler}
          />
        </div>
        <div>
          <input
            name="picture"
            type="file"
            accept="image/*"
            onChange={onConfirmPasswordHandler}
          />
        </div>
        <button type="submit" onClick={onSubmit}>가입하기</button>
      </form>
    </div>
  );
}

export default RegisterPage;
