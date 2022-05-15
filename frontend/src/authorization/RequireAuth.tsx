import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './AuthProvider';

const RequireAuth = ({ children }: { children: JSX.Element }) => {
    const auth = useAuth();
    const location = useLocation();

    if (!auth.user || !auth.token) {
        return <Navigate to='/login' state={{ from: location }} replace />;
    }

    return children;
}
export default RequireAuth;
