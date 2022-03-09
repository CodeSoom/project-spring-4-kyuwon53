import { Link } from "react-router-dom";

function Header() {
  return (
    <header>
      <div>
        <div> Booklog </div>
        <nav>
          <ul>
            <li>
              <Link to={'/'}>모아보기</Link>
            </li>
            <li>
              <Link to={'/sign-up'}>회원가입</Link>
            </li>
            <li>
              <Link to={'/sign-in'}>로그인</Link>
            </li>
            <li>
              <Link to={`/posts`}>글쓰기</Link>
            </li>
          </ul>
        </nav>
      </div>
    </header>
  );
}

export default Header;