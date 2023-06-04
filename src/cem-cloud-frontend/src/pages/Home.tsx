import Installations from "../installation/Installations"

const Home = (props: any) => {
    return <Installations apiUri={props.apiUri}></Installations>
};

export default Home;