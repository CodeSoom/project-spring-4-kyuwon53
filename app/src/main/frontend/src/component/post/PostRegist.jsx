import React, { useState, useEffect } from "react";
import axios from 'axios';
import { useAsync } from './useAsync';

function PostRegist() {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [author, setAuthor] = useState('');
  const [email, setEmail] = useState('');

  const headers = {
    'Content-Type':'application/json;charset=UTF-8',
    'Authorization': 'Bearer accesstoken'
  }
  const handleSubmit = (event) => {
    event.preventDefault();
    const postRequestData = {
      title,
      content,
      author,
      email
    }
    registPost(postRequestData);
  }
  const onChange = (event) => {
    const id = event.target.id;

    if (id === 'post_title') {
      setTitle(event.target.value);
    }
    if (id === 'post_content') {
      setContent(event.target.value);
    }
    if (id === 'post_author') {
      setAuthor(event.target.value);
    }
    if (id === 'post_email') {
      setEmail(event.target.value);
    }
  }

  async function registPost(postRequestData) {
    const response = await axios.post(
      `http://localhost:8080/posts`,
      postRequestData
    )
    console.log(response.data);
    return response.data;
  }

  return (
    <div>
      <form onSubmit={handleSubmit}>
        <div>
          <label for="post_title" >제목</label>
          <input
            id="post_title"
            type="text"
            value={title}
            placeholder="제목을 입력하세요."
            onChange={onChange}
          />
          <label for="post_author" >작가</label>
          <input
            id="post_author"
            type="text"
            value={author}
            placeholder="저자명"
            onChange={onChange}
          />
          <label for="post_email" >작성자</label>
          <input
            id="post_email"
            type="text"
            value={email}
            placeholder="작성자"
            onChange={onChange}
          />
        </div>
        <label for="post_content" >내용</label>
        <textarea
          id="post_content"
          type="content"
          value={content}
          placeholder="내용을 입력하세요."
          onChange={onChange}
        />
        <button>작성완료</button>
      </form>
    </div>
  );
}

export default PostRegist;
