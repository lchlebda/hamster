import AuthStatus from './authorization/AuthStatus';
import { Link, Outlet } from 'react-router-dom';

function Layout() {
    return (
        <div>
            <AuthStatus />

            <ul>
                <li>
                    <Link to="/">Home</Link>
                </li>
            </ul>

            <Outlet />
        </div>
    );
}
export default Layout;