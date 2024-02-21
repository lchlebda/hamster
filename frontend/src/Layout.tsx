import AuthStatus from './authorization/AuthStatus';
import {Link, Outlet} from 'react-router-dom';
import {FC, ReactElement} from 'react';

const Layout: FC = (): ReactElement => {
  return (
    <div>
      <AuthStatus/>
      <div className='menu'>
        <Link to="/">Home</Link>
        <Link to="/weekView">Week view</Link>
      </div>
      <Outlet/>
    </div>
  );
}
export default Layout;