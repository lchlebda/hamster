import { useAuth } from './AuthProvider';
import { useNavigate } from 'react-router-dom';

const AuthStatus = () => {
    let auth = useAuth();
    let navigate = useNavigate();

    if (!auth.user || !auth.token) {
        return <p>You are not logged in.</p>;
    }

    return (
        <p>
            Welcome { auth.user }!{ " " } Your token is { auth.token }
            <button
                onClick={() => {
                    auth.signOut(() => navigate("/"));
                }}
            >
                Sign out
            </button>
        </p>
    );
}
export default AuthStatus;