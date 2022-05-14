import React from 'react';

interface AuthContextType {
    user: any;
    token: string;
    signIn: (user: string, navigate: VoidFunction, callback: VoidFunction) => void;
    signOut: (callback: VoidFunction) => void;
}

const AuthContext = React.createContext<AuthContextType>(null!);

export const useAuth = () => {
    return React.useContext(AuthContext);
}

const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = React.useState<any>(null);
    const [token, setToken] = React.useState<any>(null);

    const signIn = async (newUser: string, navigate: VoidFunction, callback: Function) => {
        const response = await callback();
        const token = await response.text();
        setToken(token);
        setUser(newUser);
        navigate();
    };

    const signOut = (callback: VoidFunction) => {
        callback();
        setUser(null);
        setToken(null);
    };

    const value = { user, token, signIn, signOut };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export default AuthProvider;