import { PrimeIcons } from "primereact/api";
import { Button } from "primereact/button";
import "./ComponentListEntry.css";

const ComponentListEntry = ({ label, selectAction, deleteAction, className }: { label: string, selectAction?: Function, deleteAction: Function, className? : string }) => {
    return (
        <div className={`component-list-entry container ${className}`}>
            {
                !selectAction
                ? <Button severity="secondary" outlined disabled>{label}</Button>
                : <Button onClick={() => selectAction()}>{label}</Button>
            }
            <Button severity="danger" onClick={() => deleteAction()}><i className={PrimeIcons.TRASH} /></Button>
        </div>
    )
};

export default ComponentListEntry;