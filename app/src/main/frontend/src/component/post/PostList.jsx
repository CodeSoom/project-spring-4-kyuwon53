import React, { useState, useEffect } from "react";
import axios from 'axios';
import { useAsync } from './useAsync';
import Post from './Post';

async function getPosts() {
  const response = await axios.get(
    'http://localhost:8080/posts',
  );
  return response.data;
}

function PostList() {
  const [state, setState] = useAsync(getPosts, [], true);
  const [postId, setPostId] = useState(null);
  const { loading, data: posts, error } = state;

  if (loading) return <div>Loading...</div>
  if (error) return <div>Error!!</div>
  if (!posts) return <button onClick={setState}>불러오기</button>;
  return (
    <div>
      {posts.map((post) => (
        <div>
          <ul>
            <li key={post.id} onClick={() => setPostId(post.id)}>
              {post.title} ({post.author})
            </li>
          </ul>
          <button onClick={setState}>불러오기</button>
          {postId && <Post id={postId} />}
        </div>
      ))
      }
    </div>
  )
}

export default PostList;
