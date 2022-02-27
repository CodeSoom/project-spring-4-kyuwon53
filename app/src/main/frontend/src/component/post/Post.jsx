import React from "react";
import axios from 'axios';
import { useAsync } from "./useAsync";

async function getPost(id) {
  const response = await axios.get(
    `http://localhost:8080/post/${id}`,
  );
  return response.data;
}

function Post({ id }) {
  const [state] = useAsync(() => getPost(id), [id]);
  const { loading, data: post, error } = state;

  if (loading) return <div>Loading...</div>
  if (error) return <div>Error!!</div>
  if (!post) return null;

  return (
    <div>
      <div>
        <h2>{post.title}</h2>
        <h4>{post.author}</h4>
      </div>
      <div>
        <p>{post.content}</p>
      </div>
    </div>
  );
}

export default Post;
