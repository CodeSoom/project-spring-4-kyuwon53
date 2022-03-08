import './App.css';
import { useEffect, useState } from "react";
import {
  BrowserRouter as Router,
  Switch,
  Route
} from "react-router-dom";
import PostList from './component/post/PostList';
import RegisterPage from './component/user/RegisterPage';
import Login from './component/user/Login';
import PostRegist from './component/post/PostRegist';

function App() {
  const [message, setMessage] = useState([]);

  useEffect(() => {
    fetch("/")
      .then((response) => {
        return response.json();
      })
      .then(function (data) {
        setMessage(data);
      });
  }, []);

  return (
    <div className="App">
      <Router>
        <Switch>
          <Route path="/posts">
            <PostRegist />
          </Route>
          <Route path="/postList">
            <PostList />
          </Route>
          <Route path="/sign-up">
            <RegisterPage />
          </Route>
          <Route path="/sign-in">
            <Login />
          </Route>
        </Switch>
      </Router>
    </div>
  );
}

export default App;
