import React from 'react';

interface AuthContextType {
    user: any;
    signIn: (user: string, callback: VoidFunction) => void;
    signOut: (callback: VoidFunction) => void;
}

const AuthContext = React.createContext<AuthContextType>(null!);

const fakeAuthProvider = {
    isAuthenticated: false,
    signIn(callback: VoidFunction) {
        fakeAuthProvider.isAuthenticated = true;
        setTimeout(callback, 100); // fake async
    },
    signOut(callback: VoidFunction) {
        fakeAuthProvider.isAuthenticated = false;
        setTimeout(callback, 100);
    },
};

export const useAuth = () => {
    return React.useContext(AuthContext);
}

const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    let [user, setUser] = React.useState<any>(null);

    let signIn = (newUser: string, callback: VoidFunction) => {
        return fakeAuthProvider.signIn(() => {
            setUser(newUser);
            callback();
        });
    };

    let signOut = (callback: VoidFunction) => {
        return fakeAuthProvider.signOut(() => {
            setUser(null);
            callback();
        });
    };

    let value = { user, signIn, signOut };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export default AuthProvider;