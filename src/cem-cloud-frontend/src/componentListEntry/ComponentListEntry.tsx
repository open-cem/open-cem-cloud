import { PrimeIcons } from "primereact/api";
import { Button } from "primereact/button";
import "./ComponentListEntry.css";

const ComponentListEntry = ({ label, selectAction, deleteAction }: { label: string, selectAction:Function, deleteAction: Function }) => {
    return (
        <div className="component-list-entry container">
            <Button onClick={() => selectAction()}>{label}</Button>
            <Button severity="danger" onClick={() => deleteAction()}><i className={PrimeIcons.TRASH} /></Button>
        </div>
    )
};

export default ComponentListEntry;