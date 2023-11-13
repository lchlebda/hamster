import AuthStatus from './authorization/AuthStatus';
import { Link, Outlet } from 'react-router-dom';
import { FC, ReactElement } from 'react';

const Layout: FC = (): ReactElement =>  {
    return (
        <div>
            <AuthStatus />
            <Link to="/">Home</Link><br/>
            <Link to="/weekView">Week view</Link>
            <Outlet />
        </div>
    );
}
export default Layout;